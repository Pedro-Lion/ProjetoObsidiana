export function Label(props) {
  const icone = () => {
    switch (props.type) {
      case "email":
        return "email";

      case "password":
        return "cadeado";

      default:
        return "pessoa";
    }
  };

  return (
    <label className="w-full mt-6 first-of-type:mt-0 flex flex-col justify-between">
      <span className="mb-1.5">{props.children}</span>

      <div className="h-15 border border-gray-300 rounded-xl flex">
        <input
          className="w-full h-full p-4 pt-0 pb-0 focus:outline-black rounded-xl accent-transparent placeholder-gray-300"
          type={props.type}
          placeholder={props.placeholder}
          onInput={(e) => props.onInput(e.target.value)}
        />

        <div className="h-full aspect-square rounded-xl flex-none flex justify-center items-center bg-[#0a0840]">
          <img
            className="h-9.5"
            src={"/icons/" + icone() + ".svg"}
            alt={"Ícone de " + icone()}
          />
        </div>
      </div>
    </label>
  );
}
