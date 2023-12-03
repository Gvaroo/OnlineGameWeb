import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyRoomsComponent } from './my-rooms.component';

describe('MyRoomsComponent', () => {
  let component: MyRoomsComponent;
  let fixture: ComponentFixture<MyRoomsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyRoomsComponent]
    });
    fixture = TestBed.createComponent(MyRoomsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
