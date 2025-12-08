export default function DashboardKpi({ kpis }) {
    return (
        <div className="w-full p-4 bg-purple-50">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <div className="bg-white p-8 rounded-lg shadow">
                    Orçamentos Aprovados: {kpis.aprovados}
                </div>

                <div className="bg-white p-8 rounded-lg shadow">
                    Orçamentos Pendentes: {kpis.pendentes}
                </div>

                <div className="bg-white p-8 rounded-lg shadow">
                    Orçamentos Concluídos: {kpis.concluidos}
                </div>
            </div>
        </div>
    );
}
