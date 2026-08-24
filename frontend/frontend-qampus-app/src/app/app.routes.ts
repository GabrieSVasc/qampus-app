import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Unauthorized } from './auth/unauthorized/unauthorized';
import { authGuard } from './auth/auth-guard';
import { CreatePost } from './post/create-post/create-post';
import { Home } from './post/home/home';
import { Duvida } from './post/duvida/duvida';
import { EditPost } from './post/edit-post/edit-post';
import { Profile } from './profile/profile';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'home',
    component: Home,
    canActivate: [authGuard],
    data: {role: 'STUDENT'}
  },
  {
    path: 'registrar',
    component: Register
  },
  {
    path: 'unauthorized',
    component: Unauthorized
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'post/criar',
    component: CreatePost,
    canActivate: [authGuard],
    data: {role: 'STUDENT'}
  },
  {
    path: 'post/:idPost',
    component: Duvida,
    canActivate: [authGuard],
    data: {role: 'STUDENT'}
  },
  {
    path: 'post/editar/:id',
    component: EditPost,
    canActivate: [authGuard],
    data: {role: 'STUDENT'}
  },
  {
    path: 'perfil',
    component: Profile,
    canActivate: [authGuard]
  },
  {
    path: 'post/editar/:idPost/comentario/:idComentario',
    component: Duvida,
    canActivate: [authGuard],
  },
];