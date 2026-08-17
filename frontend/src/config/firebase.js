import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";

const firebaseConfig = {
  apiKey: "AIzaSyAsvD6c_D9wsBdaRXWa8hG77shMzQg1y6c",
  authDomain: "swiftcart-3822f.firebaseapp.com",
  projectId: "swiftcart-3822f",
  storageBucket: "swiftcart-3822f.firebasestorage.app",
  messagingSenderId: "444189686245",
  appId: "1:444189686245:web:9ff7788ae490d44473570c",
  measurementId: "G-0PXG8MDWXN"
};

const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);