import { Pie } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import { faker } from 'faker';
import DashboardKpi from '../components/Kpis/DashboardKpi';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

export function Dashboard() {
  const data = {
    labels: ['Aprovados', 'Aguardando', 'Rejeitados'],
    datasets: [
      {
        label: '# of Votes',
        data: [5, 2, 3],
        backgroundColor: [
          'rgba(168, 85, 247, 0.3)',   // lilás médio
          'rgba(139, 92, 246, 0.3)',   // roxo suave
          'rgba(196, 181, 253, 0.3)',  // lavanda
        ],
        borderColor: [
          'rgba(168, 85, 247, 1)',     // lilás médio
          'rgba(139, 92, 246, 1)',     // roxo
          'rgba(196, 181, 253, 1)',    // lavanda
        ],
        borderWidth: 1,
      },
    ],
  };

  const labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

  const dataLinha = {
    labels,
    datasets: [
      {
        label: 'Lucro por mês',
        data: [1, 4, 6, 8, 10, 12, 14],
        borderColor: 'rgba(139, 92, 246, 1)',        // roxo
        backgroundColor: 'rgba(139, 92, 246, 0.4)',  // roxo suave
      },
    ],
  };

  return (
    <>
      <h1 className="">Dashboard</h1>
      <DashboardKpi />
      <div className='flex w-full justify-between gap-15 justify-center'>
        <div className='flex w-full p-10 border-3 rounded-lg border-purple-200'>
          <Line data={dataLinha} />
        </div>
        <div className='flex w-full h-200 border-3 rounded-lg justify-center p-10 border-purple-200'>
          <Pie data={data} />
        </div>
      </div>
    </>
  );
}
