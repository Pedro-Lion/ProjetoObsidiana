export default function DashboardKpi({ kpis }) {
    return (
        <div className="w-full p-4 bg-purple-50">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">

                <div className="bg-white p-8 rounded-lg shadow">
                    Orçamentos <b className="text-amber-400">Pendentes</b>: {kpis.pendentes}
                </div>

                <div className="bg-white p-8 rounded-lg shadow">
                    Orçamentos <b className="text-green-500">Confirmados</b>: {kpis.confirmados}
                </div>

                <div className="bg-white p-8 rounded-lg shadow">
                    Orçamentos <b className="text-red-400">Cancelados</b>: {kpis.cancelados}
                </div>
            </div>
        </div>
    );
}
