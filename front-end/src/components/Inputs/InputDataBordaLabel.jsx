import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";

export function InputDataBordaLabel({
  titulo = "", placeholder = "Insira a data", value, defaultValue, className = "w-80", onChange
}) {
  return (
    <div className={"flex flex-col " + className}>
      <label
        className="relative top-3 ml-[0.7rem] px-[0.3rem]
        text-indigo-500 font-medium text-[1.1rem]
        bg-white w-fit z-99"
      >
        {titulo}
      </label>
      <Datetime
        locale="pt-br"
        dateFormat="DD/MM/YYYY"
        timeFormat="HH:mm"
        closeOnSelect={true}
        className="border-indigo-500 text-slate-700
        px-3 py-3 text-[1.1rem] bg-transparent border-1 rounded-lg"
        inputProps={{
          className: "w-full placeholder:text-black/25 focus:outline-none ",
          placeholder: placeholder,
        }}
        value={value}
        initialValue={defaultValue}
        onChange={onChange ?? undefined}
      />   
    </div>
  );
}
