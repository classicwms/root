import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImbatchserialNewComponent } from './imbatchserial-new.component';

describe('ImbatchserialNewComponent', () => {
  let component: ImbatchserialNewComponent;
  let fixture: ComponentFixture<ImbatchserialNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImbatchserialNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImbatchserialNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
