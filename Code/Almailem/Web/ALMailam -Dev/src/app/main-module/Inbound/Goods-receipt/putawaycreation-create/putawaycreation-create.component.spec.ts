import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PutawaycreationCreateComponent } from './putawaycreation-create.component';

describe('PutawaycreationCreateComponent', () => {
  let component: PutawaycreationCreateComponent;
  let fixture: ComponentFixture<PutawaycreationCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PutawaycreationCreateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PutawaycreationCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
