/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    screens: {
      // --- Breakpoints normais (mobile-first) ---
      sm: '500px',   // smartphones maiores
      md: '850px',   // tablets e pequenos notebooks
      lg: '1100px',  // laptops
      xl: '1440px',  // monitores padrão desktop

      // --- Breakpoints inversos (até tal tamanho) ---
      'max-sm': { 'max': '499px' },
      'max-md': { 'max': '849px' },
      'max-lg': { 'max': '1099px' },
    },

    extend: {},
  },
  plugins: [],
}
