import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OponnentComponent } from './oponnent.component';

describe('OponnentComponent', () => {
  let component: OponnentComponent;
  let fixture: ComponentFixture<OponnentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OponnentComponent]
    });
    fixture = TestBed.createComponent(OponnentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
