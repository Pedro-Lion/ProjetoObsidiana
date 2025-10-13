import React, { useState } from 'react';

export function InputCheckbox(props) {
    const [isChecked, setIsChecked] = useState(false);
    const handleCheckboxChange = (event) => {
        setIsChecked(event.target.checked);
    };

    return (
        <div className="flex flex-row w-fit items-center">
            <input
                type="checkbox"
                checked={isChecked}
                onChange={handleCheckboxChange}
                className="accent-violet-200 hover:accent-violet-400 hover:cursor-pointer"/>
            <label className="w-fit h-auto pl-2
            text-slate-700 font-normal text-[1.2rem]">
            {props.texto}
            </label>
        </div>
    );
}
