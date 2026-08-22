import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditTurma } from './edit-turma';

describe('EditTurma', () => {
  let component: EditTurma;
  let fixture: ComponentFixture<EditTurma>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditTurma],
    }).compileComponents();

    fixture = TestBed.createComponent(EditTurma);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
