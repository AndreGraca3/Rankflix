/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {},
    colors: {
      "primary-bg": "var(--primary-bg)",
      "secondary-bg": "var(--secondary-bg)",
      "primary-fg": "rgba(255, 255, 255, 0.9)",
      "secondary-fg": "rgba(12, 11, 17, 1)",
    },
  },
  plugins: [],
};
