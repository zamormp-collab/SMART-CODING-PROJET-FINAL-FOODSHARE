// Fonction centrale pour tous les appels HTTP
const API_BASE = '/api';
const TOKEN_KEY = 'foodshare_token';

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {

  const token = localStorage.getItem(TOKEN_KEY);

  const incomingHeaders = (options.headers as Record<string, string>) || {};

  const hasContentType = Object.keys(incomingHeaders).some(
    key => key.toLowerCase() === 'content-type'
  );

  const headers: Record<string, string> = {
    ...(hasContentType ? {} : { 'Content-Type': 'application/json' }),
    // Fix ngrok 
    // Bypasse la page d'avertissement HTML que ngrok injecte avant les requêtes
    'ngrok-skip-browser-warning': 'true',
    // 
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...incomingHeaders,
  };

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
    credentials: 'include',
  });

  if (!response.ok) {
    let message = `Erreur ${response.status}`;
    try {
      const errorData = await response.json();
      message = errorData.message || errorData.error || message;
    } catch {
      try {
        const textMessage = await response.text();
        if (textMessage) message = textMessage;
      } catch { /* Échec de lecture */ }
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}