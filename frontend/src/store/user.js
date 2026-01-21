import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    username: '',
    roles: [],
  }),
  actions: {
    setUser({ token, username, roles }) {
      this.token = token;
      this.username = username;
      this.roles = roles || [];
      localStorage.setItem('token', token);
      localStorage.setItem('username', username);
      localStorage.setItem('roles', JSON.stringify(this.roles));
    },
    logout() {
      this.token = '';
      this.username = '';
      this.roles = [];
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('roles');
    },
    loadFromStorage() {
      const rawToken = localStorage.getItem('token');
      const rawUsername = localStorage.getItem('username');
      const rawRoles = localStorage.getItem('roles');

      this.token = rawToken && rawToken !== 'undefined' && rawToken !== 'null' ? rawToken : '';
      this.username = rawUsername && rawUsername !== 'undefined' && rawUsername !== 'null' ? rawUsername : '';
      this.roles = rawRoles && rawRoles !== 'undefined' && rawRoles !== 'null'
        ? JSON.parse(rawRoles)
        : [];
    },
    hasRole(role) {
      return this.roles.includes(role);
    },
    isAdmin() {
      return this.roles.includes('admin');
    },
  },
});
