const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Serve static web files from public folder
app.use(express.static(path.join(__dirname, 'public')));

// Catch-all route to redirect to index.html for SPA router
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`=================================================`);
  console.log(`R.I.M.A Web Controller running at:`);
  console.log(`👉 http://localhost:${PORT}`);
  console.log(`=================================================`);
});
