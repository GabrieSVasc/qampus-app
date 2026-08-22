import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListDisciplina } from './list-disciplina';

describe('ListDisciplina', () => {
  let component: ListDisciplina;
  let fixture: ComponentFixture<ListDisciplina>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListDisciplina],
    }).compileComponents();

    fixture = TestBed.createComponent(ListDisciplina);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
