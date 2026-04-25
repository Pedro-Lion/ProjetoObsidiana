export function Foto({ icone = "bi bi-person", tamanho = "8", src = null }) {
  return (
    <label
      style={{ width: `${tamanho}rem`, height: `${tamanho}rem` }}
      className="flex-none relative rounded-full flex items-center justify-center
        p-[0.25rem] bg-gradient-to-b from-fuchsia-300 via-violet-500 to-sky-200"
    >
      {src ? (
        <img
          src={src}
          alt="Foto de perfil"
          className="rounded-full h-full w-full object-cover"
        />
      ) : (
        <div className="rounded-full h-full w-full bg-slate-100 text-center content-center">
          <i className={`${icone} text-zinc-500 text-5xl`}></i>
        </div>
      )}
    </label>
  );
}