import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '../views/Login.vue';
import DashboardPage from '../views/Dashboard.vue';
import EditSubestacao from '../views/EditSubestacao.vue';
import CreateSubestacao from '../views/CreateSubestacao.vue';


const routes = [
  { path: '/', component: LoginPage },
  { path: '/dashboard', component: DashboardPage },
  { path: '/editar/:id', component: EditSubestacao, props: true },
  { path: '/criar', component: CreateSubestacao }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;