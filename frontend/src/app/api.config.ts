/**
 * Runtime-overridable API base URL.
 * A deployment can define globalThis.__OPSTRACK_API_URL__ before Angular boots.
 */
const runtimeConfig = globalThis as typeof globalThis & {
  __OPSTRACK_API_URL__?: string;
};

export const API_BASE_URL =
  runtimeConfig.__OPSTRACK_API_URL__ ?? 'http://localhost:8081';
