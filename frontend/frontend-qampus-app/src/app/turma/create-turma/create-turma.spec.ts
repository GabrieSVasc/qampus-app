import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateTurma } from './create-turma';

describe('CreateTurma', () => {
  let component: CreateTurma;
  let fixture: ComponentFixture<CreateTurma>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTurma],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateTurma);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
