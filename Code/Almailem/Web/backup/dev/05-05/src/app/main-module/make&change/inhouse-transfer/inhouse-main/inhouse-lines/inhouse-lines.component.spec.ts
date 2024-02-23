import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InhouseLinesComponent } from './inhouse-lines.component';

describe('InhouseLinesComponent', () => {
  let component: InhouseLinesComponent;
  let fixture: ComponentFixture<InhouseLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InhouseLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InhouseLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
