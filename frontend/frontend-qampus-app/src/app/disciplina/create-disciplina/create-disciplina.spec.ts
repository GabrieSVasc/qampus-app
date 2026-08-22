import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateDisciplina } from './create-disciplina';

describe('CreateDisciplina', () => {
  let component: CreateDisciplina;
  let fixture: ComponentFixture<CreateDisciplina>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateDisciplina],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateDisciplina);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
