import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InhouseNewComponent } from './inhouse-new.component';

describe('InhouseNewComponent', () => {
  let component: InhouseNewComponent;
  let fixture: ComponentFixture<InhouseNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InhouseNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InhouseNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
