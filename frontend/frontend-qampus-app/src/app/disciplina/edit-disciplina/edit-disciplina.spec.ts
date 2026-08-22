import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditDisciplina } from './edit-disciplina';

describe('EditDisciplina', () => {
  let component: EditDisciplina;
  let fixture: ComponentFixture<EditDisciplina>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditDisciplina],
    }).compileComponents();

    fixture = TestBed.createComponent(EditDisciplina);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
