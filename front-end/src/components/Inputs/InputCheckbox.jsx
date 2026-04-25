import { useState } from "react";

export function InputCheckbox({ texto, className, onChange }) {
  const [isChecked, setIsChecked] = useState(false)
  
  function handleCheck(e) {
    setIsChecked(e.target.checked)
    if (onChange) onChange(e)
  }

  return (
    <label className={"flex flex-row items-center cursor-pointer " + className}>
      <input
        type="checkbox"
        checked={isChecked}
        onChange={handleCheck}
        className="accent-violet-200 hover:accent-violet-400 hover:cursor-pointer"
      />
      <span className="w-fit h-auto pl-2
        text-slate-700 font-normal text-[1.2rem]">
        {texto}
      </span>
    </label>
  );
}
