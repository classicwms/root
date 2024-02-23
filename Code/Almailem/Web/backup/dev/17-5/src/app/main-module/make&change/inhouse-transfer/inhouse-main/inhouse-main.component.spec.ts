import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InhouseMainComponent } from './inhouse-main.component';

describe('InhouseMainComponent', () => {
  let component: InhouseMainComponent;
  let fixture: ComponentFixture<InhouseMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InhouseMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InhouseMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
