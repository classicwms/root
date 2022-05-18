import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkutoskuComponent } from './skutosku.component';

describe('SkutoskuComponent', () => {
  let component: SkutoskuComponent;
  let fixture: ComponentFixture<SkutoskuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SkutoskuComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SkutoskuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
