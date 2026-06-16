import { ThemeService } from './theme.service';

describe('ThemeService', () => {
  let service: ThemeService;
  let body: HTMLElement;
  let storageValues: Map<string, string>;
  let storage: Storage;

  beforeEach(() => {
    body = document.createElement('body');
    storageValues = new Map<string, string>();
    storage = {
      getItem: (key: string) => storageValues.get(key) ?? null,
      setItem: (key: string, value: string) => {
        storageValues.set(key, value);
      },
      removeItem: (key: string) => {
        storageValues.delete(key);
      },
      clear: () => storageValues.clear(),
      key: (index: number) => Array.from(storageValues.keys())[index] ?? null,
      get length() {
        return storageValues.size;
      }
    } as Storage;

    service = new ThemeService({
      body,
      defaultView: {
        localStorage: storage
      }
    } as Document);
  });

  it('defaults to the light theme when no saved preference exists', () => {
    service.initTheme();

    expect(service.isDarkMode()).toBe(false);
    expect(body.classList.contains('theme-light')).toBe(true);
    expect(body.classList.contains('theme-dark')).toBe(false);
    expect(storage.getItem('theme_preference')).toBe('light');
  });

  it('applies a saved dark theme preference', () => {
    storage.setItem('theme_preference', 'dark');

    service.initTheme();

    expect(service.isDarkMode()).toBe(true);
    expect(body.classList.contains('theme-dark')).toBe(true);
    expect(body.classList.contains('theme-light')).toBe(false);
  });

  it('toggles the theme and persists the new preference', () => {
    service.initTheme();

    service.toggleTheme();

    expect(service.isDarkMode()).toBe(true);
    expect(body.classList.contains('theme-dark')).toBe(true);
    expect(storage.getItem('theme_preference')).toBe('dark');

    service.toggleTheme();

    expect(service.isDarkMode()).toBe(false);
    expect(body.classList.contains('theme-light')).toBe(true);
    expect(storage.getItem('theme_preference')).toBe('light');
  });
});
