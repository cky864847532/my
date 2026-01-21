import { createApp } from 'vue';
import './style.css';
import App from './App.vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import pinia from './store';
import axios from 'axios';

import router from './router';
import { setupAxiosInterceptors } from './utils/auth';

// In production (e.g. GitHub Pages), the frontend is hosted on a different origin.
// Configure the backend origin via VITE_API_BASE at build time.
if (!import.meta.env.DEV) {
	const apiBase = import.meta.env.VITE_API_BASE;
	if (apiBase) {
		axios.defaults.baseURL = String(apiBase).replace(/\/+$/, '');
	}
}

const app = createApp(App);
app.use(ElementPlus);
app.use(pinia);
app.use(router);

setupAxiosInterceptors();
app.mount('#app');
