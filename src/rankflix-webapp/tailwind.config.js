/** @type {import('tailwindcss').Config} */

module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        "primary-bg": "var(--primary-bg)",
        "secondary-bg": "var(--secondary-bg)",
        "primary-fg": "rgba(255, 255, 255, 0.9)",
        "secondary-fg": "rgba(12, 11, 17, 1)",
      },
      keyframes: {
        heartbeat: {
          "0%": { transform: "scale(1)" },
          "50%": { transform: "scale(1.05)" },
          "100%": { transform: "scale(1)" },
        },
      },
      animation: {
        heartbeat: "heartbeat 1s ease-in-out infinite",
      },
    },
  },
  plugins: [],
};
