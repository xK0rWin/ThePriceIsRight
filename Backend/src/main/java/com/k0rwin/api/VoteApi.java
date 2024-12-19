package com.k0rwin.api;

import com.k0rwin.dto.*;
import com.k0rwin.entity.Item;
import com.k0rwin.entity.Player;
import com.k0rwin.entity.Vote;
import com.k0rwin.repository.ItemRepository;
import com.k0rwin.repository.PlayerRepository;
import com.k0rwin.repository.VoteRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vote")
public class VoteApi {

    private final List<SseEmitter> emitters = new ArrayList<>();
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    VoteMapper voteMapper;
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    PlayerMapper playerMapper;

    @GetMapping(value = "/sse", produces = "text/event-stream")
    public SseEmitter handleSse() {
        SseEmitter emitter = new SseEmitter(-1L);
        emitters.add(emitter);

        // Remove the emitter when the client disconnects
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public void sendSseEvent(String message) {
        Iterator<SseEmitter> iterator = emitters.iterator();
        while (iterator.hasNext()) {
            SseEmitter emitter = iterator.next();
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                iterator.remove();
            }
        }
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<PlayerDTO> register(@RequestBody String userName) {
        Optional<Player> px = playerRepository.findById(userName);
        if (!px.isPresent()) {
            Player player = new Player(userName);
            playerRepository.save(player);

            sendSseEvent("newUser");
            return new ResponseEntity<>(playerMapper.toDTO(player), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(playerMapper.toDTO(px.get()), HttpStatus.OK);
        }

    }

    @GetMapping(value = "/currentVote", produces = "application/json")
    public ResponseEntity<ItemDTO> getCurrentlyVotedItem() {
        return getActiveItem().map(item -> new ResponseEntity<>(itemMapper.toDTO(item), HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/{itemName}", produces = "application/json")
    public ResponseEntity<List<VoteDTO>> getVotesOfItem(@PathVariable String itemName) {
        Item item = itemRepository.getById(itemName);
        return new ResponseEntity<>(item.getVotes().stream().map(voteMapper::toDTO).collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(value = "/items", produces = "application/json")
    public ResponseEntity<List<ItemDTO>> getItems() {
        List<Item> items = itemRepository.findAll();
        return new ResponseEntity<>(items.stream().map(itemMapper::toDTO).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping(value = "/close", produces = "text/plain")
    public ResponseEntity<String> closeRound() {
        getActiveItem().ifPresent(item -> {
            Item i = getActiveItem().get();
            finishRound(i);
            i.setStatus("COMPLETED");
            this.itemRepository.save(getActiveItem().get());
            sendSseEvent("endVote");
        });

        return new ResponseEntity<>("Round closed", HttpStatus.OK);
    }

    @PostMapping(value = "/start/{itemName}", produces = "text/plain")
    public ResponseEntity<String> startNewVote(@PathVariable String itemName) {

        getActiveItem().ifPresent(item -> {
            Item i = getActiveItem().get();
            finishRound(i);
            i.setStatus("COMPLETED");
            this.itemRepository.save(getActiveItem().get());
        });

        Optional<Item> item = itemRepository.findById(itemName);
        if (item.isEmpty()) {
            return new ResponseEntity<>("Item not found", HttpStatus.BAD_REQUEST);
        } else {
            item.get().setStatus("ACTIVE");
            this.itemRepository.save(item.get());
        }

        sendSseEvent("startVote");
        return new ResponseEntity<>("Voting started", HttpStatus.OK);
    }

    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<Long> addVote(@RequestBody VoteDTO voteDto) {
        Vote vote = voteMapper.toEntity(voteDto);

        if (vote.getPlayer().getVotes().stream().anyMatch(v -> Objects.equals(v.getItem().getName(), vote.getItem().getName()))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        vote.setId(null);
        Vote v = voteRepository.save(vote);
        sendSseEvent("addVote");
        return ResponseEntity.ok(v.getId());
    }

    @PostMapping(value = "/item", produces = "application/json")
    public ResponseEntity<Long> addItem(@RequestBody ItemDTO itemDto) {
        Item i = itemRepository.save(itemMapper.toEntity(itemDto));
        return new ResponseEntity<>(i.getId(), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/clear", produces = "text/plain")
    public ResponseEntity<String> clearDatabase() {
        itemRepository.deleteAll();
        voteRepository.deleteAll();
        playerRepository.deleteAll();

        sendSseEvent("endVote");
        return ResponseEntity.ok("Database cleared successfully.");
    }

    public void finishRound(Item ix) {
        Item item = itemRepository.getById(ix.getName());
        double actualPrice = item.getPrice();
        List<Vote> votes = item.getVotes();

        votes.sort(new Comparator<Vote>() {
            @Override
            public int compare(Vote vote1, Vote vote2) {
                double diff1 = Math.abs(vote1.getPriceGuess() - actualPrice);
                double diff2 = Math.abs(vote2.getPriceGuess() - actualPrice);

                return Double.compare(diff1, diff2);
            }
        });

        out: for (int i = 0; i < votes.size(); i++) {
            Vote vote = votes.get(i);
            if (Objects.equals(vote.getPriceGuess(), ix.getPrice())) {
                Player p = vote.getPlayer();
                p.setScore(p.getScore() + 3);
                playerRepository.save(p);
            } else {
                Player p = vote.getPlayer();
                if (i < 3) {
                    if (i > 0) {
                        Vote prevVote = votes.get(i - 1);
                        if (Objects.equals(vote.getPriceGuess(), prevVote.getPriceGuess())) {
                            p.setScore(p.getScore() + (3 - i + 1));
                        } else {
                            p.setScore(p.getScore() + (3 - i));
                        }
                    } else {
                        p.setScore(p.getScore() + (3 - i));
                    }
                    playerRepository.save(p);
                } else {
                    Vote prevVote = votes.get(i - 1);
                    if (Objects.equals(vote.getPriceGuess(), prevVote.getPriceGuess())) {
                        p.setScore(p.getScore() + 1);
                        playerRepository.save(p);
                    } else {
                        break out;
                    }
                }
            }
        }
    }

    public Optional<Item> getActiveItem() {
        List<Item> activeItems = this.itemRepository.findByStatus("ACTIVE");
        if (!activeItems.isEmpty()) {
            return Optional.of(activeItems.get(0));
        } else {
            return Optional.empty();
        }
    }
}
