<template>
  <div class="container">
    <h1>Dashboard</h1>
    <button @click="incluirSubestacao">Incluir Subestação</button>
    <table>
      <thead>
        <tr>
          <th>Código</th>
          <th>Nome</th>
          <th>Excluir</th>
          <th>Alterar</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="subestacao in subestacoes" :key="subestacao.id">
          <td>{{ subestacao.codigo }}</td>
          <td>{{ subestacao.nome }}</td>
          <td><button @click="alterarSubestacao(subestacao.id)">Editar</button></td>
          <td><button @click="excluirSubestacao(subestacao.id)">Excluir</button></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'DashboardPage',
  data() {
    return {
      subestacoes: []
    };
  },
  methods: {
    async fetchSubestacoes() {
      try {
        const response = await axios.get('http://localhost:8081/subestacao');
        console.log('Response data:', response.data);
        this.subestacoes = response.data;
      } catch (error) {
        console.error('Erro ao buscar subestações:', error);
      }
    },
    alterarSubestacao(id) {
      this.$router.push(`/editar/${id}`);
    },
    exibirNoMapa(latitude, longitude) {
      console.log(`Exibir no mapa: Latitude ${latitude}, Longitude ${longitude}`);
      // Adicione a lógica para exibir a subestação no mapa
    },
    incluirSubestacao() {
      this.$router.push('/criar');
    }
  },
  mounted() {
    this.fetchSubestacoes();
  }
};
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #333;
}

button {
  padding: 10px 20px;
  border: none;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  border-radius: 5px;
  font-size: 16px;
}

button:hover {
  background-color: #0056b3;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  padding: 10px;
  border: 1px solid #ccc;
  text-align: left;
}

th {
  background-color: #f2f2f2;
}
</style>