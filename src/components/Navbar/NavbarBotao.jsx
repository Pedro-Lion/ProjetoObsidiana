import { useNavigate } from "react-router-dom";
import { Dropdown } from "../Icons/Dropdown.jsx";

export function NavbarBotao(props) {
  const navigate = useNavigate();

  const onClick = props.onClick ? props.onClick : () => navigate("/")
  
  return (
    <button
      onClick={onClick}
      className="w-full pt-2 pb-2 pl-3 pr-3 rounded-xl flex justify-between items-center"
    >
      <div className="flex gap-4 items-center text-2xl">
        {props.children[0]}
        {props.children[1]}
      </div>

      {props.temDrop && (
        <Dropdown
          style={props.estiloDrop}
          className="h-3 stroke-gray-50 transition-[transform] duration-350"
        />
      )}
    </button>
  );
}
