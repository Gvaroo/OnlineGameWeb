import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { registerLocaleData } from '@angular/common';
import { NZ_I18N } from 'ng-zorro-antd/i18n';
import { en_US } from 'ng-zorro-antd/i18n';
import en from '@angular/common/locales/en';
import { FormsModule } from '@angular/forms';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { NzInputNumberModule } from 'ng-zorro-antd/input-number';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzNotificationModule } from 'ng-zorro-antd/notification';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzRateModule } from 'ng-zorro-antd/rate';
import { NzCarouselModule } from 'ng-zorro-antd/carousel';
import { NzPopconfirmModule } from 'ng-zorro-antd/popconfirm';
import { HeaderComponent } from './components/header/header.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { FooterComponent } from './components/footer/footer.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { ChunkPipe } from './pipes/chunk.pipe';
import { AuthGuard } from './guards/auth.guard';

import { NzSelectModule } from 'ng-zorro-antd/select';

import { RxStompService } from './rx-stomp.service';
import { rxStompServiceFactory } from './rx-stomp-service-factory';
import { CreatorComponent } from './components/creator/creator.component';
import { OponnentComponent } from './components/oponnent/oponnent.component';
import { RoomsComponent } from './components/rooms/rooms.component';
import { MyRoomsComponent } from './components/my-rooms/my-rooms.component';

registerLocaleData(en);

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    FooterComponent,
    ChunkPipe,
    CreatorComponent,
    OponnentComponent,
    RoomsComponent,
    MyRoomsComponent,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    NzButtonModule,
    NzDropDownModule,
    NzIconModule,
    NzInputModule,
    NzAlertModule,
    NzInputNumberModule,
    NzPopconfirmModule,
    NzSpinModule,
    NzNotificationModule,
    NzSelectModule,
    NzProgressModule,
    NzTableModule,
    NzRateModule,
    NzCarouselModule,
    NoopAnimationsModule,
  ],
  providers: [
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
    },
    { provide: NZ_I18N, useValue: en_US },
    AuthGuard,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
