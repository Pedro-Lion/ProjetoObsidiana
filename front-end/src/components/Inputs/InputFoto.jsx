import { useState } from "react";

export function InputFoto({ onChange, icone = "bi bi-camera", tamanho = "8" }) {
    const [preview, setPreview] = useState(null);

    function handleFileChange(e) {
        const file = e.target.files[0];
        if (!file) return;

        const imageUrl = URL.createObjectURL(file);
        setPreview(imageUrl);
        if (onChange) onChange(e);
    }

    return (
        <>
            <label
                htmlFor="avatarUpload"
                style={{ width: `${tamanho}rem`, height: `${tamanho}rem` }}
                className={`relative rounded-full
                   cursor-pointer hover:opacity-80 transition-all duration-200 
                   flex items-center justify-center
                   p-[0.25rem] bg-gradient-to-b from-fuchsia-300 via-violet-500 to-sky-200`}>
                {preview ? (
                    <>
                    <img src={preview} className="object-cover w-full h-full rounded-full bg-red-600 backdrop-blur-lg" />
                    </>
                ) : (
                    <div className="rounded-full h-full w-full
                    bg-slate-100 text-center content-center">
                        <i className={`${icone} text-zinc-500 text-5xl`}></i>
                    </div>
                )}

                <div className="absolute inset-0 bg-black/60 opacity-0 hover:opacity-100
                rounded-full transition-all flex items-center justify-center text-white
                m-[0.25rem] text-sm text-center">
                    {preview ? "Alterar foto" : "Escolher foto"}
                </div>
            </label>

            <input
                id="avatarUpload"
                type="file"
                accept=".png, .jpg, .jpeg"
                onChange={handleFileChange}
                className="hidden"
            />
        </>
    );
}