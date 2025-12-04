export function Foto({ icone = "bi bi-camera", tamanho = "8" }) {
//     const tamanhoNum = parseFloat(tamanho);
//     return (
//         <>
//             <style>
//                 {`
//             .icone-foto{
//             width:${tamanhoNum}rem;
//             height:${tamanhoNum}rem;
//             }
            
//             @media(max-width:900px){
//             .icone-foto{
//             width: ${tamanhoNum * 0.75}rem;
//             height: ${tamanhoNum * 0.75}rem;
//             }
//             `}
//             </style>
//             <label
//                 className={`icone-foto
//                     relative rounded-full aspect-square flex items-center justify-center
//                     p-1 sm:p-0.5 bg-gradient-to-b from-fuchsia-300 via-violet-500 to-sky-200
//                    `}>

//                 <div className={`rounded-full aspect-square w-full h-auto
//                     bg-slate-100 text-center content-center`}>
//                     <i className={`${icone} text-zinc-500 text-5xl`}></i>
//                 </div>
//             </label>
//         </>
//     );
// }
  return (
    <>
      <label
        style={{ width: `${tamanho}rem`, height: `${tamanho}rem` }}
        className="flex-none relative rounded-full 
        flex items-center justify-center
        p-[0.25rem] bg-gradient-to-b 
        from-fuchsia-300 via-violet-500 to-sky-200"
      >
        <div
          className="rounded-full h-full w-full
          bg-slate-100 text-center content-center"
        >
          <i className={`${icone} text-zinc-500 text-5xl`}></i>
        </div>
      </label>
    </>
  );
}
