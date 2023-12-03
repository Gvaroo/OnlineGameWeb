import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';

import { AuthGuard } from './guards/auth.guard';

import { CreatorComponent } from './components/creator/creator.component';
import { OponnentComponent } from './components/oponnent/oponnent.component';
import { RoomsComponent } from './components/rooms/rooms.component';
import { MyRoomsComponent } from './components/my-rooms/my-rooms.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    // canActivate: [HomeRedirectGuard],
  },
  {
    path: 'register',
    component: RegisterComponent,
    // canActivate: [HomeRedirectGuard],
  },

  {
    path: 'rooms',
    component: RoomsComponent,
  },
  {
    path: 'myRooms',
    component: MyRoomsComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'ct/:roomUid',
    component: CreatorComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'op/:roomUid',
    component: OponnentComponent,
    canActivate: [AuthGuard],
  },
  { path: '', component: HomeComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
