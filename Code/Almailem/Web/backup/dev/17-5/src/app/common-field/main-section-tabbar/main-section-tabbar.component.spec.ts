import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainSectionTabbarComponent } from './main-section-tabbar.component';

describe('MainSectionTabbarComponent', () => {
  let component: MainSectionTabbarComponent;
  let fixture: ComponentFixture<MainSectionTabbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MainSectionTabbarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MainSectionTabbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
