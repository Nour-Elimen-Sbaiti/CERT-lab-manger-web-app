# CERT Lab Manager — Frontend

React + Vite interface for the Spring Boot API in `../calibration-hub`.

```bash
npm install
npm run dev
```

The dev server runs on `http://localhost:5173` and proxies API calls to the backend on port `8081`. Start PostgreSQL and the Spring application first. To point a deployed frontend at a different API, set `VITE_API_URL` (see `.env.example`).

Features: JWT login, dashboard, equipment search/status updates, equipment creation/editing, intervention history, calibration laboratories, personnel, alerts, document uploads, and role-aware administration.
