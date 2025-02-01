<template>
  <div class="container">
    <h1>Criar Nova Subestação</h1>
    <form @submit.prevent="createSubestacao">
      <div class="form-group">
        <label for="codigo">Código:</label>
        <input type="text" id="codigo" v-model="subestacao.codigo" required />
      </div>
      <div class="form-group">
        <label for="nome">Nome:</label>
        <input type="text" id="nome" v-model="subestacao.nome" required />
      </div>
      <div class="form-group">
        <label for="latitude">Latitude:</label>
        <input type="number" id="latitude" v-model="subestacao.latitude" step="any" required />
      </div>
      <div class="form-group">
        <label for="longitude">Longitude:</label>
        <input type="number" id="longitude" v-model="subestacao.longitude" step="any" required />
      </div>
      <h2>Adicionar Redes</h2>
      <div class="form-group">
        <label for="redeNome">Nome da Rede:</label>
        <input type="text" id="redeNome" v-model="novaRede.nome" />
      </div>
      <div class="form-group">
        <label for="redeCodigo">Código da Rede:</label>
        <input type="text" id="redeCodigo" v-model="novaRede.codigo" />
      </div>
      <button type="button" @click="addRede">Adicionar Rede</button>
      <h2>Redes Adicionadas</h2>
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Código</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(rede, index) in redes" :key="index">
            <td>{{ rede.nome }}</td>
            <td>{{ rede.codigo }}</td>
            <td><button type="button" @click="removeRede(index)">Remover</button></td>
          </tr>
        </tbody>
      </table>
      <div class="button-group">
        <button type="submit">Salvar</button>
        <button type="button" @click="cancelar">Cancelar</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'CreateSubestacao',
  setup() {
    const router = useRouter();
    const subestacao = ref({
      codigo: '',
      nome: '',
      latitude: '',
      longitude: ''
    });
    const redes = ref([]);
    const novaRede = ref({
      nome: '',
      codigo: ''
    });

    const addRede = () => {
      if (novaRede.value.nome && novaRede.value.codigo) {
        redes.value.push({ ...novaRede.value });
        novaRede.value.nome = '';
        novaRede.value.codigo = '';
      }
    };

    const removeRede = (index) => {
      redes.value.splice(index, 1);
    };

    const createSubestacao = async () => {
      try {
        await axios.post('http://localhost:8081/subestacao', {
          ...subestacao.value,
          redes: redes.value
        });
        console.log('Subestação criada com sucesso');
        router.push('/dashboard');
      } catch (error) {
        console.error('Erro ao criar subestação:', error);
      }
    };

    const cancelar = () => {
      router.push('/dashboard');
    };

    return {
      subestacao,
      novaRede,
      redes,
      addRede,
      removeRede,
      createSubestacao,
      cancelar
    };
  }
};
</script>

<style scoped>
.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

h1, h2 {
  text-align: center;
  color: #333;
}

form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  font-weight: bold;
  margin-bottom: 5px;
}

input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 16px;
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

.button-group {
  display: flex;
  justify-content: space-between;
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

button[type="button"] {
  background-color: #6c757d;
}

button:hover {
  background-color: #0056b3;
}

button[type="button"]:hover {
  background-color: #5a6268;
}
</style>