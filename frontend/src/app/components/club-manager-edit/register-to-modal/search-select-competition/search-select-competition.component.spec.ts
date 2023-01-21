import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchSelectCompetitionComponent } from './search-select-competition.component';

describe('SearchSelectCompetitionComponent', () => {
  let component: SearchSelectCompetitionComponent;
  let fixture: ComponentFixture<SearchSelectCompetitionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchSelectCompetitionComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchSelectCompetitionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
