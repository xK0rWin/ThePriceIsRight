import { Routes } from '@angular/router';
import { AuthGuard } from '../authguard';
import { LoginViewComponent } from './login-view/login-view.component';
import { UserViewComponent } from './user-view/user-view.component';
import { AdminViewComponent } from './admin-view/admin-view.component';
import { AddItemComponent } from './add-item/add-item.component';

export const routes: Routes = [
    {path: 'login', component: LoginViewComponent},
    {path: '', component: UserViewComponent, canActivate: [AuthGuard]},
    {path: 'admin', component: AdminViewComponent, canActivate: [AuthGuard]},
    {path: 'add', component: AddItemComponent, canActivate: [AuthGuard]}
];
