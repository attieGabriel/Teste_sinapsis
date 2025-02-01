<template>
  <div class="login-page">
    <div class="login-container">
      <h1>Login</h1>
      <form @submit.prevent="login">
        <div class="input-group">
          <label for="usuario">Usuário</label>
          <input type="text" id="usuario" v-model="usuario" required placeholder="Digite seu usuário" />
        </div>
        <div class="input-group">
          <label for="password">Senha</label>
          <input type="password" id="password" v-model="password" required placeholder="Digite sua senha" />
        </div>
        <button type="submit">Entrar</button>
      </form>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router';

export default {
  name: "LoginPage",
  setup() {
    const router = useRouter();
    const login = () => {
      router.push('/dashboard');
    };

    return { login };
  },
  data() {
    return {
      backgroundImage: require('@/assets/background.jpg') // Caminho correto
    };
  }
};
</script>

<style scoped>
.login-page {
  background-image: v-bind("`url(${backgroundImage})`");
  background-size: cover;
  background-position: center;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-container {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 300px;
}

.input-group {
  margin-bottom: 15px;
}

input {
  width: 100%;
  padding: 8px;
  margin-top: 5px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #333;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #555;
}
</style>