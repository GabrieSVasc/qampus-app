import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTurma } from './list-turma';

describe('ListTurma', () => {
  let component: ListTurma;
  let fixture: ComponentFixture<ListTurma>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListTurma],
    }).compileComponents();

    fixture = TestBed.createComponent(ListTurma);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
