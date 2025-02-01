<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style scoped>
h1 {
    font-family: Arial, sans-serif;
    color: #333;
    margin-bottom: 20px;
  }
  
  ul {
    list-style-type: none;
    padding: 0;
  }
  
  li {
    background-color: #f4f4f4;
    margin: 10px 0;
    padding: 10px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  strong {
    font-size: 1.2rem;
    color: #333;
  }
  
  p {
    font-size: 1rem;
    color: #666;
  }
</style>