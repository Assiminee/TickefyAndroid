# Tickefy Android — Entry Capture Interface 📸

This Android application is the **entry-point UI** for the [Tickefy](https://github.com/Assiminee/Tickefy) system. It allows users at stadium gates to **capture a facial image** which is then sent to your backend (FastAPI) for authentication or dataset enrichment.


---

## 🧩 Purpose & Role in the Ecosystem

- The app **does not perform facial recognition locally** — all AI logic lives on the server side (FastAPI).  
- Main responsibilities:
  1. Provide a clean camera interface for users or staff to take photos at the gate  
  2. Send the captured image to the FastAPI endpoint for validation  
  3. Optionally contribute the images to your biometric dataset for further training / improvement  
- In the overall architecture, it serves as the **bridge between the user in the real world and the AI backend**.

---

## 🛠 Tech Stack & Dependencies

| Component | Details |
|-----------|---------|
| **Platform** | Android (Java) |
| **Minimum SDK / Target** | Android 10+ |
| **Libraries Used** | CameraX, Retrofit, Glide (or similar image preview/rendering) |
| **Network** | Sends HTTP requests (e.g. `POST /validate-face`) to your backend |

---

## 🚀 How to Run / Test the App

> ⚠️ **Important**
> 
> This repository is tightly-coupled with the [tickefy](https://github.com/Assiminee/Tickefy) eco-system.
> In order to run the system, start by cloning this repository:
> ```bash
> git clone https://github.com/Assiminee/TickefyAndroid.git
> ```
> then visit the [tickefy](https://github.com/Assiminee/Tickefy) repository and follow the detailed instruction in the README.md file.
