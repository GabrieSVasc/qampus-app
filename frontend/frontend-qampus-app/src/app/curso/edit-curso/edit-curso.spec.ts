import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCurso } from './edit-curso';

describe('EditCurso', () => {
  let component: EditCurso;
  let fixture: ComponentFixture<EditCurso>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditCurso],
    }).compileComponents();

    fixture = TestBed.createComponent(EditCurso);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
