import { STORAGE_KEYS } from './constants';

const localStorageKeys = [STORAGE_KEYS.PORTAL];

function resolveStorage(key: string): Storage {
  return localStorageKeys.includes(key) ? localStorage : sessionStorage;
}

export const storage = {
  getItem(key: string): string | null {
    return resolveStorage(key).getItem(key);
  },
  setItem(key: string, value: string): void {
    resolveStorage(key).setItem(key, value);
  },
  removeItem(key: string): void {
    resolveStorage(key).removeItem(key);
  },
};
