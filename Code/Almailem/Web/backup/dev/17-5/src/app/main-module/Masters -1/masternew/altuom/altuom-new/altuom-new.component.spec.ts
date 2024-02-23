import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AltuomNewComponent } from './altuom-new.component';

describe('AltuomNewComponent', () => {
  let component: AltuomNewComponent;
  let fixture: ComponentFixture<AltuomNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AltuomNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AltuomNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
