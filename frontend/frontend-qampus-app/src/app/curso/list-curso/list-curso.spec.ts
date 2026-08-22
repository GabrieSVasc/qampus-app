import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListCurso } from './list-curso';

describe('ListCurso', () => {
  let component: ListCurso;
  let fixture: ComponentFixture<ListCurso>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListCurso],
    }).compileComponents();

    fixture = TestBed.createComponent(ListCurso);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
