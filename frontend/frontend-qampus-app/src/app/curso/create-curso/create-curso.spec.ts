import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCurso } from './create-curso';

describe('CreateCurso', () => {
  let component: CreateCurso;
  let fixture: ComponentFixture<CreateCurso>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateCurso],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateCurso);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
