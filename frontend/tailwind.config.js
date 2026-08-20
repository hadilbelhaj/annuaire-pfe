/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        blue: {
          500: '#0070CC', // Adjust this to match your exact blue
          900: '#1a365d',
        },
        'background': '#9c88ff',
        'navbar-dark-primary': '#18283b',
        'navbar-dark-secondary': '#2c3e50',
        'navbar-light-primary': '#f5f6fa',
        'navbar-light-secondary': '#8392a5',
        sky: {
          400: '#38BDF8',
        }
      }, fontFamily: {
        'poppins': ['Poppins', 'sans-serif'],
      },
      transitionProperty: {
        'height': 'height',
        'spacing': 'margin, padding',
      }
    },
  },
  plugins: [],
}

