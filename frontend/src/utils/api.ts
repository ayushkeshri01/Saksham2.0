export async function fetchWithAuth(url: string, options: RequestInit = {}) {
  const token = typeof window !== 'undefined' ? localStorage.getItem('portal_token') : null;
  
  const headers = new Headers(options.headers);
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const res = await fetch(url, { ...options, headers });

  if (res.status === 401 || res.status === 403) {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('portal_token');
      localStorage.removeItem('portal_user');
      window.location.href = '/login';
    }
  }

  return res;
}
