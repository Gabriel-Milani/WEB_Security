const path = require('path');
const express = require('express');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT || 3000;
const USER_SERVICE_URL = process.env.USER_SERVICE_URL || 'http://localhost:8081';

app.use(express.urlencoded({ extended: false }));
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.post('/send-code', async (req, res) => {
  const email = req.body.email;

  if (!email) {
    return res.redirect('/?error=Informe%20um%20e-mail');
  }

  try {
    await axios.post(`${USER_SERVICE_URL}/auth/request-code`, { email });
    return res.redirect(`/verify?email=${encodeURIComponent(email)}`);
  } catch (error) {
    return res.redirect('/?error=Nao%20foi%20possivel%20enviar%20o%20codigo');
  }
});

app.get('/verify', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'verify.html'));
});

app.post('/verify-code', async (req, res) => {
  const { email, code } = req.body;

  if (!email || !code) {
    return res.redirect(`/verify?email=${encodeURIComponent(email || '')}&error=Informe%20o%20codigo`);
  }

  try {
    const response = await axios.post(`${USER_SERVICE_URL}/auth/verify-code`, { email, code });
    const token = response.data && response.data.token;

    if (!token) {
      return res.redirect(`/verify?email=${encodeURIComponent(email)}&error=Codigo%20invalido%20ou%20expirado`);
    }

    return res.send(`<!doctype html>
<html lang="pt-BR">
<head><meta charset="utf-8"><title>Autenticado</title></head>
<body>
<script>
sessionStorage.setItem('jwtToken', ${JSON.stringify(token)});
window.location.href = '/dashboard';
</script>
</body>
</html>`);
  } catch (error) {
    return res.redirect(`/verify?email=${encodeURIComponent(email)}&error=Erro%20ao%20validar%20codigo`);
  }
});

app.get('/dashboard', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'dashboard.html'));
});

app.listen(PORT, () => {
  console.log(`Frontend rodando em http://localhost:${PORT}`);
});
